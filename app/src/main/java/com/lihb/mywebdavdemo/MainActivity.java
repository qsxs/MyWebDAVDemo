package com.lihb.mywebdavdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lihb.mywebdavdemo.network.IApi;
import com.lihb.mywebdavdemo.network.NetworkManager;
import com.lihb.mywebdavdemo.utils.Base64Util;
import com.lihb.mywebdavdemo.utils.FileUtil;
import com.lihb.mywebdavdemo.utils.UriUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    TextView tv;
    Button btnUpload;
    OkHttpClient okHttpClient;
    String value;
    RecyclerView list;
    FileListAdapter adapter;
    Context mContext;
    EditText et;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        tv = findViewById(R.id.tv);
        btnUpload = findViewById(R.id.btn_upload);
        list = findViewById(R.id.list);
        et = findViewById(R.id.et);
        adapter = new FileListAdapter();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                Tb.ResponseBean item = adapter.getItem(position);
                if (item.isDir()) {
                    propfind(item.getHref());
                } else {
                    download(item.getHref());
                    Toast.makeText(MainActivity.this, item.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                Tb.ResponseBean item = adapter.getItem(position);
                Toast.makeText(mContext, "准备删除：" + item.getDisplayName(), Toast.LENGTH_SHORT).show();
                delete(item);
                return true;
            }
        });
        list.setAdapter(adapter);

        btnUpload.setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_new_dir).setOnClickListener(this);

        initOkHttp();
        value = "Basic " + Base64Util.encode(KeyConfig.WEBDAV_USERNAME + ":" + KeyConfig.WEBDAV_PASSWORD);

        propfind("/dav/");
    }

    private void delete(Tb.ResponseBean item) {
        NetworkManager.getApiService()
                .delete(item.getHref())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> responseBodyResponse) {
                        Log.i(TAG, "onNext: ");
                        if (responseBodyResponse.isSuccessful()) {
                            propfind(tv.getText().toString());
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private void download(final String path) {
        Request request = new Request.Builder()
                .url("https://dav.jianguoyun.com" + path)
                .addHeader("Authorization", value)
                .get()
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
                } else {
                    FileUtil.writeFile(response.body().byteStream(),
                            Environment.getExternalStorageDirectory()
                                    + File.separator
                                    + App.context().getString(R.string.app_name)
                                    + path);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "下载文件成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void upload(Uri data) {

        String pathFromUri = UriUtil.getPathFromUri(MainActivity.this, data);

        File file = new File(pathFromUri);
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), file);
        Request request = new Request.Builder()
                .url("https://dav.jianguoyun.com" + tv.getText() + file.getName())
                .addHeader("Authorization", value)
                .put(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO: 2018/5/29 上传 成功，后续应该PROPFIND确认一下
                            Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

    private void propfind(final String path) {

//        NetworkManager.getApiService()
//                .propfind(path)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new DefaultObserver<DavResumeModel>() {
//                    @Override
//                    public void onNext(DavResumeModel response) {
//                        Log.i(TAG, "onNext: ");
//                        tv.setText(path);
//                        List<DavResumeModel.ResponsesBean> responses = response.getResponses();
//                        adapter.setNewData(responses.subList(1, responses.size()));
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i(TAG, "onError: ");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i(TAG, "onComplete: ");
//                    }
//                });

        NetworkManager.getApiService()
                .propfindx(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Tb>() {
                    @Override
                    public void onNext(Tb response) {
                        Log.i(TAG, "onNext:Tb ");
                        tv.setText(path);
                        List<Tb.ResponseBean> responses = response.getList();
                        for (Tb.ResponseBean respons : responses) {
                            long lastModifiedDate = respons.getLastModifiedDate().getTime();
                            Log.i(TAG, "onNext: " + respons.getDisplayName() + "," + lastModifiedDate);
                        }
                        adapter.setNewData(responses.subList(1, responses.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });

//        Request request = new Request.Builder()
//                .url("https://dav.jianguoyun.com" + path)
//                .addHeader("Authorization", value)
//                .method("PROPFIND", null)
//                .build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i(TAG, "onFailure: ");
//            }
//
//            @Override
//            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
//                Log.i(TAG, "onResponse: ");
//                if (!response.isSuccessful()) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    return;
//                }
//                InputStream in = response.body().byteStream();
//                if (in != null) {
//                    // read response and try to build a xml document
//                    try {
//                        Document document = DomUtil.parseDocument(in);
//                        MultiStatus xml = MultiStatus.createFromXml(document.getDocumentElement());
//                        Status[] status = xml.getResponses()[0].getStatus();
//                        final DavResumeModel davResumeModel = DavResumeModel.create(xml);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tv.setText(path);
//                                List<Tb.ResponseBean> responses = davResumeModel.getResponses();
//                                adapter.setNewData(responses.subList(1, responses.size()));
//                            }
//                        });
//                        Log.i(TAG, "onResponse: ");
////                                xml.getResponses()[0]
//                    } catch (ParserConfigurationException e) {
//                        IOException exception =
//                                new IOException("XML parser configuration error");
//                        exception.initCause(e);
//                        throw exception;
//                    } catch (SAXException e) {
//                        IOException exception = new IOException("XML parsing error");
//                        exception.initCause(e);
//                        throw exception;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        throw e;
//                    } finally {
//                        in.close();
//                    }
//                }
//            }
//        });
    }

    private void createDir(String dirName) {
        // TODO: 2018/5/30 0030 应该先propfind查询文件夹是否已经存在
        NetworkManager.getApiService()
                .mkol(tv.getText().toString() + dirName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Object>() {
                    @Override
                    public void onNext(Object davResumeModel) {
                        Log.i(TAG, "onNext: ");
                        // TODO: 2018/5/30 0030 应该先propfind查询是否确定新建成功
                        propfind(tv.getText().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
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
            case R.id.btn:
                try {
                    String s = tv.getText().toString();
                    String substring1 = s.substring(0, s.length() - 1);
                    String substring = substring1.substring(0, substring1.lastIndexOf("/"));
                    propfind(substring);
                } catch (Exception e) {
                    e.printStackTrace();
                    propfind("/dav/");
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_upload:
                startPickerFile();
                break;
            case R.id.btn_new_dir:
                if (!TextUtils.isEmpty(et.getText())) {
                    createDir(et.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    static int REQUEST_FILE_CODE = 1024;

    private void startPickerFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_CODE && resultCode == RESULT_OK) {
            upload(data.getData());
        }
    }
}