package a.b.c.manager;

import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import a.b.c.R;

public class MoreManager implements View.OnClickListener {

	private ProgressBar moreProgressBar;
	private TextView moreTextView;
	private View moreView;
	private
	@Nullable
	MoreCallback moreCallback;
	private boolean hasMore, autoRefresh = true, tempAutoRefresh = true;

	private MoreManager() {
	}

	private MoreManager(@NonNull LayoutInflater layoutInflater) {
		moreView = layoutInflater.inflate(R.layout.layout_more, null);
		moreView.setOnClickListener(this);
		moreProgressBar = (ProgressBar) moreView.findViewById(R.id.pb_more);
		moreTextView = (TextView) moreView.findViewById(R.id.tv_more);
		moreTextView.setOnClickListener(this);
	}

	public MoreManager(@NonNull ListView listView) {
		this(LayoutInflater.from(listView.getContext()));
		if (listView.getFooterViewsCount() <= 0) listView.addFooterView(moreView, null, false);

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (visibleItemCount + firstVisibleItem == totalItemCount) {
					if (hasMore && autoRefresh && moreCallback != null) {
						autoRefresh = false;
						start();
					}
				}
			}
		});
	}

	private MoreManager(@NonNull RecyclerView recyclerView) {
		this(LayoutInflater.from(recyclerView.getContext()));
		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
				int visibleItemCount = layoutManager.getChildCount();
				int totalItemCount = layoutManager.getItemCount();
				int pastItems = layoutManager.findFirstVisibleItemPosition();
				if ((pastItems + visibleItemCount) >= totalItemCount) {
					if (hasMore && autoRefresh && moreCallback != null) {
						autoRefresh = false;
						start();
					}
				}
			}
		});
	}

	public void setCallback(@Nullable MoreCallback moreCallback) {
		this.moreCallback = moreCallback;
	}

	@CheckResult
	public View getView() {
		return moreView;
	}

	@Override
	public void onClick(View v) {
		if (!autoRefresh && moreCallback != null && v == moreTextView) {
			start();
		}
	}

	private void start() {
		moreTextView.setEnabled(false);
		moreProgressBar.setVisibility(View.VISIBLE);
		moreTextView.setText("正在加载中…");
		if (moreCallback != null) {
			moreCallback.more();
		}
	}

	public void autoRefresh(boolean autoRefresh) {
		tempAutoRefresh = autoRefresh;
		this.autoRefresh = autoRefresh;
	}

	public void complete(boolean hasMore) {
		this.hasMore = hasMore;
		if (tempAutoRefresh) autoRefresh = tempAutoRefresh;
		moreView.setBackgroundResource(android.R.color.transparent);
		moreTextView.setVisibility(View.VISIBLE);
		moreProgressBar.setVisibility(View.GONE);
		if (hasMore) {
			moreTextView.setEnabled(true);
			moreTextView.setText("点击加载更多");
			moreTextView.setBackgroundResource(R.drawable.selector_pressed_transparent);
		} else {
			moreTextView.setEnabled(false);
			moreTextView.setText("已全部加载完毕");
			moreTextView.setBackgroundResource(android.R.color.transparent);
		}
	}



	public static abstract class MoreAdapter extends RecyclerView.Adapter implements MoreAdapterCallback {

		private MoreManager moreManager;
		private OnItemClickListener onItemClickListener;
		private MoreCallback moreCallback;

		public interface OnItemClickListener {
			void onItemClick(View itemView, int position);
		}

		public void setMoreCallback(MoreCallback moreCallback) {
			this.moreCallback = moreCallback;
		}

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
		}

		@CallSuper
		@Override
		public void onAttachedToRecyclerView(RecyclerView recyclerView) {
			super.onAttachedToRecyclerView(recyclerView);
			moreManager = new MoreManager(recyclerView);
			moreManager.setCallback(moreCallback);
		}

		public void complete(boolean hasMore) {
			moreManager.complete(hasMore);
		}

		@Override
		public int getItemCount() {
			return count() + 1;
		}

		@Override
		public int getItemViewType(int position) {
			return (position == count()) ? 1 : 0;
		}

		@CallSuper
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == 0) {
				return create(parent, viewType);
			} else {
				return new MoreViewHolder(moreManager.getView());
			}
		}

		@CallSuper
		@Override
		public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
			if (position < count()) {
				bind(holder, position);
			} else {
				if (onItemClickListener != null) {
					holder.itemView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							onItemClickListener.onItemClick(holder.itemView, position);
						}
					});
				}
			}
		}

		final static class MoreViewHolder extends RecyclerView.ViewHolder {
			public MoreViewHolder(@NonNull View itemView) {
				super(itemView);
			}
		}
	}

	public interface MoreCallback {
		void more();
	}

	public interface MoreAdapterCallback {
		@CheckResult
		RecyclerView.ViewHolder create(@NonNull ViewGroup parent, int viewType);

		void bind(@NonNull RecyclerView.ViewHolder holder, int position);

		@CheckResult
		int count();
	}
}
