package com.lihb.mywebdavdemo;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FileListAdapter extends BaseQuickAdapter<Tb.ResponseBean, BaseViewHolder> {

    public FileListAdapter() {
        super(R.layout.item_file_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Tb.ResponseBean item) {
        helper.setText(R.id.tv_name, item.getDisplayName())
                .setText(R.id.tv_size, item.getContentLength());
        if (item.isDir()) {
            helper.setText(R.id.tv_type, "夹");
        } else {
            helper.setText(R.id.tv_type, "");
        }
    }
}
