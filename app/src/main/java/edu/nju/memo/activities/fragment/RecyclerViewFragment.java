package edu.nju.memo.activities.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.nju.memo.R;
import edu.nju.memo.view.Record;
import edu.nju.memo.view.SwipeItemLayout;

/**
 * Author： liyi
 * Date：    2017/2/26.
 */

public class RecyclerViewFragment extends Fragment {
    private View root;
    private List<Record> recordList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(root==null){
            root = inflater.inflate(R.layout.activity_item_list,container,false);
            RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_record);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
            recyclerView.setAdapter(new MyAdapter(getContext(), initRecordList()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));

        }
        return root;
    }

    private List<Record> initRecordList() {
        recordList = new ArrayList<>();
        for(int i = 0; i < 10; i ++) {
            Record record = new Record(R.mipmap.ic_launcher, "2017/10/6 20:5" + i);
            recordList.add(record);
        }
        return recordList;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {
        private Context mContext;
        private List<Record> recordList;

        public MyAdapter(Context context, List<Record> recordList) {
            this.mContext = context;
            this.recordList = recordList;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(mContext).inflate(R.layout.single_item, parent, false);
            return new Holder(root);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Record record = recordList.get(position);
            holder.recordTime.setText(record.getDate());
            holder.recordImage.setImageResource(record.getImageId());
        }

        @Override
        public int getItemCount() {
            return recordList.size();
        }

        class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            ImageView recordImage;
            TextView recordTime;

            Holder(View itemView) {
                super(itemView);

                recordImage = (ImageView) itemView.findViewById(R.id.iv_record);
                recordTime = (TextView) itemView.findViewById(R.id.tv_date);

                View main = itemView.findViewById(R.id.main);
                main.setOnClickListener(this);
                main.setOnLongClickListener(this);

                View delete = itemView.findViewById(R.id.delete);
                delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.main:
                        Toast.makeText(v.getContext(), "点击了main，位置为：" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.delete:
                        int pos = getAdapterPosition();
                        recordList.remove(pos);
                        notifyItemRemoved(pos);
                        /**
                         * TODO
                         * 删掉对应数据
                         */
                        break;
                }
            }

            @Override
            public boolean onLongClick(View v) {
                switch (v.getId()) {
                    case R.id.main:
                        Toast.makeText(v.getContext(), "长按了main，位置为：" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        }
    }

}
