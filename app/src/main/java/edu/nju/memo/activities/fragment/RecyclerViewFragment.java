package edu.nju.memo.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import java.util.List;

import edu.nju.memo.R;
import edu.nju.memo.activities.MemoDetailActivity;
import edu.nju.memo.dao.CachedMemoDao;
import edu.nju.memo.domain.Memo;
import edu.nju.memo.view.SwipeItemLayout;

/**
 * Author： liyi
 * Date：    2017/2/26.
 */

public class RecyclerViewFragment extends Fragment {
    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(root==null){
            root = inflater.inflate(R.layout.activity_item_list,container,false);
            RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_record);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
            recyclerView.setAdapter(new MyAdapter(getContext(), initMemoList()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemoDetailActivity.class);
                intent.putExtra("memo", CachedMemoDao.INSTANCE.select(1));
                startActivity(intent);
            }
        });
    }

    private List<Memo> initMemoList() {
        return CachedMemoDao.INSTANCE.selectAll();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {
        private Context mContext;
        private List<Memo> memoList;

        public MyAdapter(Context context, List<Memo> memoList) {
            this.mContext = context;
            this.memoList = memoList;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(mContext).inflate(R.layout.single_item, parent, false);
            return new Holder(root);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Memo memo = memoList.get(position);
            holder.memoTitle.setText(memo.getMTitle());
            holder.memoTime.setText("" + memo.getCreateTime());
        }

        @Override
        public int getItemCount() {
            return memoList.size();
        }

        class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView memoTitle;
            TextView memoTime;

            Holder(View itemView) {
                super(itemView);

                memoTitle = (TextView) itemView.findViewById(R.id.tv_title);
                memoTime = (TextView) itemView.findViewById(R.id.tv_date);

                View main = itemView.findViewById(R.id.main);
                main.setOnClickListener(this);

                View delete = itemView.findViewById(R.id.delete);
                delete.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                Memo memo = memoList.get(pos);
                switch (v.getId()) {
                    case R.id.main:
                        Intent intent = new Intent(getActivity(), MemoDetailActivity.class);
                        intent.putExtra("memo", memo);
                        startActivity(intent);
                        break;

                    case R.id.delete:
                        CachedMemoDao.INSTANCE.delete(memo.getId());
                        memoList.remove(pos);
                        notifyItemRemoved(pos);
                        break;
                }
            }
        }
    }

}
