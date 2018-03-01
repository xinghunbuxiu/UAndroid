# UAndroid
Android MVP+Retrofit2.0 +RxJava  

android 模板分装 一键配置

LoadView.builder 参数详解
supportFragmentManager = context.getSupportFragmentManager();
Fragment fragments = null;  // fragment 数组
BottomNavigationItem items = null;// buttomBarLayout  按钮
mBottomLayout = -1; // 正文 布局
bottomBarLayout = -1;//底部自定义导航
hasToolbar=true; // 是否有toolBar
BaseSlideView slideView = null;// 侧滑布局
contentTop = true;// toolbar 是否置顶 
slideMenu = false;// 是否支持侧滑 菜单 与 slide 共用
swipeBack = true; //是否侧滑返回
hasBottomBar = false;// 是否有底部导航
tabSelectedListener = null; //底部导航点击监听
slide = Slide.NONE; //侧滑方向 left or right


BaseActivity  配置讲解

   public void initLoad(Builder builder) {
    activity 创建时初始化 LoadView.builder
   }
    @Override
    public void init(Bundle savedInstanceState) {
      activity onCreate() 调用，
    子类 重写 
    }
    @Override
    public boolean isBack() {
      是否显示返回 默认带返回按钮 子类如果为false
      将不显示返回按钮
        return false;
    }

    @Override
    public boolean isDoubleExit() {
    是否双击退出
        return false;
    }
    
      @Override
    public void update(Observable o, Message arg) {
       通知子类 通过 observer
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lifecycleSubject.onNext(LifeEvent.CREATE);
        super.onCreate(savedInstanceState);
        // 初始化模板页面
       layout = new LoadView.Builder(this) {
            {
                mBottomLayout = getLayoutId();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置竖屏
                requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
                initLoad(this);
            }
        }.createActivity();
        layout.addObserver(this);// 增加监听
        ButterKnife.bind(this);
        intent = layout.getIntent();
        tip = layout.getEmptyView();
        initTitleBar();
        init(savedInstanceState);
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        systemBarTintManager.setTranslucentStatus(this, true);
        if (mPresenter != null) {
            mPresenter.init(this, savedInstanceState, lifecycleSubject);
        }
  
 ---------------------------------
 下拉刷新
SpringView （listView recycleView scrollView,webview）

支持自定义 下拉效果 与 上拉效果
// 下拉刷新 
public class CustomHeadView extends HeaderView {

    ImageView ivNormalRefreshHeader;
    TextView tvNormalRefreshHeaderStatus;

    public CustomHeadView(Context context) {
        super(context);
    }

    @Override
    public void onScrollChange(StateType state) {
        switch (state) {
            case NONE:
            case PULL:
                tvNormalRefreshHeaderStatus.setText("下拉刷新...");
                break;
            case RELEASE:
                tvNormalRefreshHeaderStatus.setText("释放刷新...");
                break;
            case LOADING:
                tvNormalRefreshHeaderStatus.setText("正在刷新...");
                break;
            case LOAD_CLOSE:
                tvNormalRefreshHeaderStatus.setText("刷新结束...");
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_refresh_header_normal;
    }

    @Override
    public void initView() {
        tvNormalRefreshHeaderStatus = $(R.id.tv_normal_refresh_header_status);
        ivNormalRefreshHeader = $(R.id.iv_normal_refresh_header);

    }

    @Override
    public void Scroll(int maxY, int y) {
        int currentProgress = Math.abs((y / getHeight()));
        if (currentProgress >= 11) {
            currentProgress = 11;
        }
        ivNormalRefreshHeader.getDrawable().setLevel(currentProgress);
    }
}
  // 上拉加载
 public class CustomFootView extends FooterView {


    ImageView ivNormalRefreshFooterChrysanthemum;
    TextView tvNormalRefreshFooterStatus;

    public CustomFootView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_normal_refresh_footer;
    }

    @Override
    public void initView() {
        ivNormalRefreshFooterChrysanthemum = $(R.id.iv_normal_refresh_footer_chrysanthemum);
        tvNormalRefreshFooterStatus = $(R.id.tv_normal_refresh_footer_status);
    }

    @Override
    public void Scroll(int maxY, int y) {

    }

    @Override
    public void onScrollChange(StateType state) {
        switch (state) {
            case NONE:
                ObjectAnimator.clearAllAnimations();
            case PULL:
                tvNormalRefreshFooterStatus.setText("上拉加载...");
                break;
            case RELEASE:
                tvNormalRefreshFooterStatus.setText("释放加载...");
                break;
            case LOADING:
                tvNormalRefreshFooterStatus.setText("正在加载...");
                AnimUtil.startRotation(ivNormalRefreshFooterChrysanthemum, ViewHelper.getRotation(ivNormalRefreshFooterChrysanthemum) + 359.99f, 500, 0, -1);
                break;
            case LOAD_CLOSE:
                tvNormalRefreshFooterStatus.setText("加载完毕...");
                break;
        }
    }
}
 
-------------------------------------------
侧滑 SlideMenu

支持 左边 与右边
支持 边缘检测
支持 全屏 如 Tim
支持 侧滑跟随主页移动 和不移动 及 动画效果 如 qq 的

public class SlideLeftView extends BaseSlideView {

    public SlideLeftView(Activity activity) {
        super(activity);
    }

    @Override
    public int getLayoutId() {
        return R.layout.slide_menu_layout;
    }

    @Override
    public void init() {
    // 跟随是是否要动画
        isAnim = false; 
      // 是否 跟随 
       following = false;
      // 是否全屏
        isFullScreen=true;
       //是否允许边缘检测， 默认允许
       isEnabledEdge=false
       }

    @Override
    public void initView(View slideView) {
        TextView textView = $(R.id.textView);
        textView.setText("dddddddd");
    }


}

----------------------------------------------

public class TabsActivity extends BaseActivity<TabPresenter> {

    SlideLeftView slideLeftView;

    @Override
    public boolean isShowBack( ) {
        return false;
    }
    @Override
    public boolean isDoubleExit() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initTitle(UToolBar toolBar) {

    }

    @Override
    public void initLoad(LoadView.Builder builder) {
        slideLeftView = new SlideLeftView(this);
        builder.setSlideMenu(SlideMenu.Slide.LEFT, slideLeftView);
        builder.swipeBack = false;
        builder.hasToolbar = false;
        builder.hasBottomBar = true;
        builder.addItem(new BottomNavigationItem(R.mipmap.ic_favorites, "Home").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_friends, "Books").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_nearby, "Music").setActiveColorResource(R.color.blue)
                , new BottomNavigationItem(R.mipmap.ic_recents, "Movies & TV").setActiveColorResource(R.color.colorAccent)
                , new BottomNavigationItem(R.mipmap.ic_restaurants, "Games").setActiveColorResource(R.color.colorAccent));
        builder.addFragment(new HomeFragment(), new FirstFragment(), new SecondFragment(), new ThreeFragment(), new FourFragment());
        builder.setOnTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {

            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

}

------------------------------------
Page 列表性的封装 支持 emptyView

public class SecondFragment extends BaseFragment {
    Page.Builder builder;
    List<Integer> list = new ArrayList<>();
    public SecondFragment() {

    }

    @Override
    public void initTitle(UToolBar toolBar) {
        toolBar.setTitle("recycle_view");
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        builder = new Page.Builder<Integer>(activity) {
            @Override
            public void onBindViewData(BaseViewHolder viewHolder, int position, Integer item) {
                super.onBindViewData(viewHolder, position, item);
            }

            @Override
            public void onItemClick(View view, int position, Integer data) {
            }
        }.setAutoLoadMore(true).setRefresh(true).setDivideHeight(R.dimen.space_7);
        builder.setOnLoadingListener(onLoadingListener);
        builder.setRVAdapter(R.layout.item_ciew, list);
        layout.setContentView(builder.Build(Page.PageType.List).getRootView());
    }

    Page.OnLoadingListener onLoadingListener = new Page.OnLoadingListener() {
        @Override
        public void load(int page, final Page.OnLoadFinish onLoadFinish) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < 8; i++) {
                        list.add(R.mipmap.ic_launcher);
                    }
                    onLoadFinish.finish(list, LoadingTip.LoadStatus.FINISH);
                }
            }, 100);
        }
    };

    @Override
    public int getLayoutId() {
        return 0;
    }


}
增加第一册进入 欢迎页面
public class WelcomeActivity extends LaunchActivity {


    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_launch;
    }

    /**
     * 这里执行动画
     */
    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public Class toActivity(int what) {
        
        return what == GO_HOME ? TabsActivity.class : TabsActivity.class;
    }

}
        
        
        

