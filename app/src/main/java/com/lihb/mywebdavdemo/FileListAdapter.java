package com.lihb.mywebdavdemo;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FileListAdapter extends BaseQuickAdapter<DavResumeModel.ResponsesBean, BaseViewHolder> {

    public FileListAdapter() {
        super(R.layout.item_file_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, DavResumeModel.ResponsesBean item) {
        helper.setText(R.id.tv_name, item.getDisplayname())
                .setText(R.id.tv_size, item.getcontentlength);
        String getcontenttype = item.getGetcontenttype();
        if ("httpd/unix-directory".equals(getcontenttype)) {
            helper.setText(R.id.tv_type, "夹");
        } else {
            helper.setText(R.id.tv_type, "");
        }
    }
}
