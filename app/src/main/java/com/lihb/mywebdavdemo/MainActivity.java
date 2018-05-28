package com.lihb.mywebdavdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lihb.mywebdavdemo.network.IApi;
import com.lihb.mywebdavdemo.network.webdav.MultiStatus;
import com.lihb.mywebdavdemo.network.webdav.Status;
import com.lihb.mywebdavdemo.network.webdav.xml.DomUtil;
import com.lihb.mywebdavdemo.utils.Base64Util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    TextView tv;
    Button btnUpload;
    OkHttpClient okHttpClient;
    String value;
    RecyclerView list;
    FileListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        btnUpload = findViewById(R.id.btn_upload);
        list = findViewById(R.id.list);
        adapter = new FileListAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                DavResumeModel.ResponsesBean item = adapter.getItem(position);
                if (item.isDir()) {
                    getData(item.getHref());
                } else {
                    Toast.makeText(MainActivity.this, item.getDisplayname(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        list.setAdapter(adapter);

        btnUpload.setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = tv.getText().toString();
                    String substring1 = s.substring(0, s.length() - 1);
                    String substring = substring1.substring(0, substring1.lastIndexOf("/"));
                    getData(substring);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        initOkHttp();
        value = "Basic " + Base64Util.encode(KeyConfig.WEBDAV_USERNAME + ":" + KeyConfig.WEBDAV_PASSWORD);

        getData("/dav/");
    }

    private void getData(final String path) {
        Request request = new Request.Builder()
                .url("https://dav.jianguoyun.com" + path)
                .addHeader("Authorization", value)
                .method("PROPFIND", null)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                Log.i(TAG, "onResponse: ");
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                InputStream in = response.body().byteStream();
                if (in != null) {
                    // read response and try to build a xml document
                    try {
                        Document document = DomUtil.parseDocument(in);
                        MultiStatus xml = MultiStatus.createFromXml(document.getDocumentElement());
                        Status[] status = xml.getResponses()[0].getStatus();
                        final DavResumeModel davResumeModel = DavResumeModel.create(xml);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(path);
                                List<DavResumeModel.ResponsesBean> responses = davResumeModel.getResponses();
                                adapter.setNewData(responses.subList(1, responses.size()));
                            }
                        });
                        Log.i(TAG, "onResponse: ");
//                                xml.getResponses()[0]
                    } catch (ParserConfigurationException e) {
                        IOException exception =
                                new IOException("XML parser configuration error");
                        exception.initCause(e);
                        throw exception;
                    } catch (SAXException e) {
                        IOException exception = new IOException("XML parsing error");
                        exception.initCause(e);
                        throw exception;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    } finally {
                        in.close();
                    }
                }
            }
        });
    }

    private void initOkHttp() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
//                LogUtil.si("NetworkManager", message);
                Log.i(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        File cacheFile = new File(App.context().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(IApi.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
//                .addNetworkInterceptor(new NetworkManager.HttpCacheInterceptor())
                .cache(cache)
                .build();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:

                break;
            default:
                break;
        }
    }
}