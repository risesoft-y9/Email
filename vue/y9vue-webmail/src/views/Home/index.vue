<script lang="ts" setup>
    import { onMounted, reactive, toRefs } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getAppList, getInfoList, getTodoList } from '@/api/home';
    // 默认头像
    import Avatar from '@/assets/images/touxiang.png';
    // 时间
    import { changeTime } from '@/utils/time';
    import router from '@/router';

    const { t } = useI18n();

    // import { useSettingStore } from "@/store/modules/settingStore"
    // const settingStore = useSettingStore();

    // settingStore.device === 'mobile'

    const state = reactive({
        // 数值卡片
        cardTitle: [
            {
                name: '待办',
                endVal: 100
            },
            {
                name: '在办',
                endVal: 120
            },
            {
                name: '办结件',
                endVal: 76
            },
            {
                name: '消息',
                endVal: 280
            }
        ],
        // 待办列表
        todoListConfig: {
            listData: [],
            titleKeyName: 'descript',
            pageConfig: {
                // 分页配置，false隐藏分页
                currentPage: 1, //当前页数，
                pageSize: 7, //每页显示条目个数，
                total: 0, //总条目数
                pageSizeOpts: [7, 10, 20]
            }
        },
        y9ListRef: '',
        // 通知通告列表
        infoListConfig: {
            listData: [],
            titleKeyName: 'content',
            pageConfig: false
        },
        // 通知通告的分页
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 5, //每页显示条目个数，支持 v-model 双向绑定
            total: 0, //总条目数
            customStyle: 'default',
            pageSizeOpts: [5, 10, 20]
        },
        // 应用列表得分页
        appPageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
            total: 0, //总条目数
            pageSizeOpts: [20, 35] //每页显示个数选择器的选项设置
        },
        y9InfoListRef: '',
        // 时间 time
        currTime: '',
        // 应用列表
        appList: [],
        // 待办 loading
        todoLoading: false,
        // 通知通告loading
        infoLoading: false,
        // 应用列表 loading
        appLoading: false
        // 时间定时器
        // timer,
    });

    const {
        cardTitle,
        todoListConfig,
        y9ListRef,
        todoLoading,
        currTime,
        appList,
        infoLoading,
        appLoading,
        // timer,
        infoListConfig,
        y9InfoListRef,
        pageConfig,
        appPageConfig
    } = toRefs(state);

    onMounted(() => {
        // 待办列表
        todoInit();
        // 应用列表
        appInit();
        // 通知通告列表
        infoInit();
        // 时间的赋值
        currTime.value = changeTime();
        //  getTime(); // 一秒后
    });

    // 待办列表的请求数据
    async function todoInit() {
        todoLoading.value = true;
        let result = await getTodoList({
            page: todoListConfig.value.pageConfig.currentPage,
            rows: todoListConfig.value.pageConfig.pageSize
        });
        // 赋值
        todoListConfig.value.listData = result.data;
        todoListConfig.value.listData?.map((item) => {
            item.startRender = () => {
                return h('i', {
                    className: 'ri-todo-line',
                    style: { color: item.degree === 1 ? 'red' : 'var(--el-color-primary)' }
                });
            };
            item.titleRender = () => {
                return h('span', {}, [
                    h(
                        'span',
                        {
                            style: {
                                color: item.degree === 1 ? 'red' : 'var(--el-color-primary)',
                                marginRight: '5px',
                                fontWeight: 300
                            }
                        },
                        item.degreeName
                    ),
                    h('span', {}, item.descript)
                ]);
            };
            item.endRender = () => {
                return h('span', {}, item.time);
            };
        });
        todoListConfig.value.pageConfig.total = result.total;
        todoLoading.value = false;
    }

    // 应用列表 的请求数据
    async function appInit() {
        appLoading.value = true;
        let result = await getAppList({
            page: appPageConfig.value.currentPage,
            rows: appPageConfig.value.pageSize
        });
        appList.value = result.data;
        appPageConfig.value.total = result.total;
        appLoading.value = false;
    }

    // 通知通告的 请求数据
    async function infoInit() {
        infoLoading.value = true;
        let result = await getInfoList({
            page: pageConfig.value.currentPage,
            rows: pageConfig.value.pageSize
        });
        infoListConfig.value.listData = result.data;
        infoListConfig.value.listData?.map((item) => {
            item.startRender = () => {
                return h('i', { className: 'ri-information-line', style: { color: '#E57373' } });
            };
            item.endRender = () => {
                return h('span', {}, item.time);
            };
        });
        pageConfig.value.total = result.total;
        infoLoading.value = false;
    }

    // 待办列表 通知通告 应用列表 分页 触发
    function handlerPageSize(pageSize, type) {
        switch (type) {
            // 待办列表
            case 'todo':
                todoListConfig.value.pageConfig.pageSize = pageSize;
                todoInit();
                break;
            // 通知通告
            case 'info':
                pageConfig.value.pageSize = pageSize;
                infoInit();
                break;
            // 应用列表
            default:
                appPageConfig.value.pageSize = pageSize;
                appInit();
                break;
        }
    }

    function handlerCurrPage(currentPage, type) {
        switch (type) {
            // 待办列表
            case 'todo':
                todoListConfig.value.pageConfig.currentPage = currentPage;
                todoInit();
                break;
            // 通知通告
            case 'info':
                pageConfig.value.currentPage = currentPage;
                infoInit();
                break;
            // 应用列表
            default:
                appPageConfig.value.currentPage = currentPage;
                appInit();
                break;
        }
    }

    // 点击日期 日期修改
    function handlerChangeCalendar(obj) {
        console.log(obj, 'obj');
    }

    // 点击通知 待办列表   去除选中样式
    function handlerClick(type) {
        switch (type) {
            case 'info':
                y9InfoListRef.value.removeHighlight();
                break;
            case 'todo':
                y9ListRef.value.removeHighlight();
                break;
        }
    }

    // 点击 名字 跳转个人中心, 点击待办，在办 办结件 消息
    function handlerClickCard(index) {
        // console.log(index);

        switch (index) {
            case 0:
                // 个人中心
                router.push({ path: '/personInfo' });
                break;
            case 1:
                // 待办
                router.push({ path: '/todo' });
                break;
            case 2:
                // 在办  携带参数 就需要name
                router.push({ name: 'todoIndex', params: { type: 1 } });
                break;
            case 3:
                // 办结件
                router.push({ path: '/todo' });
                break;
            case 4:
                // 消息
                router.push({ path: '/msgRemind' });
                break;
            default:
                break;
        }
    }
</script>

<template>
    <div style="height: 100%; overflow-y: auto; overflow-x: hidden; scrollbar-width: none">
        <el-row>
            <!-- 用户信息 -->
            <el-col :span="6" class="col-value" style="margin-top: 60px">
                <!-- min-width: 428px;  -->
                <div style="width: calc(100% - 30px); display: flex">
                    <!-- 个人信息 -->
                    <el-card class="user-info">
                        <div class="user">
                            <el-avatar :src="Avatar" @click="handlerClickCard(0)" />
                            <div class="content">
                                <div class="content-top">
                                    <div class="name" @click="handlerClickCard(0)">某某某</div>
                                    <span>IP：192.168.103.103</span>
                                </div>
                                <div class="content-bottom">{{ $t('深圳研发中心') }}·{{ $t('前端开发工程师') }}</div>
                            </div>
                        </div>
                    </el-card>
                    <!-- 时间 -->
                    <el-card class="time">
                        <div>{{ $t(`${currTime[1]}`) }}</div>
                        <div>{{ currTime[0] }}</div>
                        <div>{{ currTime[2] }}</div>
                    </el-card>
                </div>
            </el-col>
            <!-- 数据展示 -->
            <!-- 待办 在办 办件 消息 -->
            <el-col :span="18" class="col-value" style="margin-top: 60px">
                <!-- :style="`background: linear-gradient(to left bottom, ${item.backgroundColor}, #fff) `" -->
                <el-card
                    v-for="(item, index) in cardTitle"
                    :key="index"
                    class="card-value"
                    @click="handlerClickCard(index + 1)"
                >
                    <img src="@/assets/images/todo.jpg" style="border-radius: 50%" />
                    <div class="number-value">
                        {{ $t(`${item.name}`) }}
                        <countTo
                            :end-val="item.endVal"
                            :start-val="0"
                            :suffix="index === 3 ? $t('条') : $t('个')"
                            duration="3000"
                        ></countTo>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="16">
                <!-- 日历日程 待办列表 -->
                <el-col :span="9" class="col-value">
                    <!-- 日历日程 -->
                    <el-card class="calendar">
                        <baidu-calendar @change="handlerChangeCalendar"></baidu-calendar>
                    </el-card>
                </el-col>
                <el-col :span="15" class="col-value">
                    <!-- 待办列表 -->
                    <el-card class="todo-list">
                        <div class="title" style="margin: 0px">
                            <span>{{ $t('待办列表') }}</span>
                            <i @click="handlerClickCard(1)">查看更多</i>
                        </div>
                        <y9List
                            ref="y9ListRef"
                            v-loading="todoLoading"
                            :config="todoListConfig"
                            @click="handlerClick('todo')"
                            @on-page-size-change="(pageSize) => handlerPageSize(pageSize, 'todo')"
                            @on-curr-page-change="(currentPage) => handlerCurrPage(currentPage, 'todo')"
                        ></y9List>
                    </el-card>
                </el-col>
                <!-- 通知通告 -->
                <el-col :span="24" class="col-value">
                    <el-card class="info">
                        <div class="title" style="margin: 0; padding: 0">
                            <span>{{ $t('通知通告') }}</span>
                            <y9Pagination
                                :config="pageConfig"
                                style="margin-bottom: 10px"
                                @current-change="(currentPage) => handlerCurrPage(currentPage, 'info')"
                                @size-change="(pageSize) => handlerPageSize(pageSize, 'info')"
                            >
                            </y9Pagination>
                        </div>

                        <y9List
                            ref="y9InfoListRef"
                            v-loading="infoLoading"
                            :config="infoListConfig"
                            @click="handlerClick('info')"
                        ></y9List>
                    </el-card>
                </el-col>
            </el-col>
            <el-col :span="8" class="col-value">
                <!-- 应用列表 -->
                <!-- <el-col :span="24" > -->
                <el-card class="app-list">
                    <div class="title">
                        <span>{{ $t('应用列表') }}</span>
                    </div>
                    <div v-loading="appLoading" class="box-list">
                        <div class="list-items">
                            <div v-for="(item, index) in appList" :key="index" class="app-item">
                                <div class="image">
                                    <img :src="item.url" />
                                </div>
                                <span>{{ item.name }}</span>
                            </div>
                        </div>
                    </div>
                    <y9Pagination
                        :config="appPageConfig"
                        style="margin-bottom: 20px; width: 100%; justify-content: space-around"
                        @current-change="(currentPage) => handlerCurrPage(currentPage, 'app')"
                        @size-change="(pageSize) => handlerPageSize(pageSize, 'app')"
                    >
                    </y9Pagination>
                </el-card>
                <!-- </el-col> -->
            </el-col>
        </el-row>
    </div>
</template>

<style lang="scss" scoped>
    .col-value {
        display: flex;
        width: 100%;
        margin-top: 30px;
        // 待办 ...
        .card-value {
            margin-right: 30px;
            width: 50%;
            height: 120px;
            // overflow: auto;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            // color: #fff;
            :deep(.el-card__body) {
                height: 120px;
                // overflow: auto;
                display: flex;
                justify-content: space-between;
                align-items: center;

                span {
                    color: var(--el-color-primary);
                    // color: #fff;
                    font-size: 16px;
                }
            }

            img {
                width: 60px;
                height: 60px;
            }

            .number-value {
                display: flex;
                flex-direction: column;
                align-items: flex-end;

                span {
                    margin-top: 10px;
                }
            }
        }

        .card-value:nth-last-child(1) {
            margin-right: 3px;
        }

        // 日历
        .calendar {
            margin-right: 30px;
            width: 100%;
            // min-width: 435px;
            height: 520px;
            overflow: auto;
            scrollbar-width: none !important;

            :deep(.el-card__body) {
                height: 518px;
                padding: 15px 10px;

                .op-calendar-pc {
                    box-shadow: none;

                    .op-calendar-pc-right {
                        display: none;
                    }

                    .op-calendar-pc-left {
                        width: 100%;
                        height: 460px;
                        overflow: auto;
                        scrollbar-width: none !important;
                        padding: 0px;
                        border: none;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-content: center;
                        float: none;
                        // font-family: fantasy;
                        .op-calendar-pc-relative .op-calendar-pc-table-selected {
                            border: 2px solid #bdbfc8;
                            background-color: var(--el-color-primary);

                            span {
                                color: var(--el-bg-color) !important;
                            }
                        }

                        .op-calendar-pc-daynumber {
                            font-size: 16px;
                        }

                        .ant-select-selector {
                            border-radius: 30px;
                            width: 100px;
                            box-shadow: 0 2px 4px #0000000d;
                            border: 1px solid var(--el-color-primary-light-7);
                            color: var(--el-input-text-color, var(--el-text-color-regular));
                        }

                        .op-calendar-pc-backtoday {
                            color: var(--el-color-primary);
                            background-color: transparent;
                            position: absolute;
                            right: 0;
                            font-size: 14px;
                        }

                        .op-calendar-pc-table td {
                            padding: 2px 0px;
                        }

                        .op-calendar-pc-select-box {
                            width: 100%;
                            display: flex;

                            .op-calendar-pc-holiday-box:last-of-type {
                                display: none;
                            }
                        }

                        .op-calendar-pc-table-box {
                            display: flex;
                            justify-content: center;
                        }
                    }

                    .op-calendar-pc-box {
                        z-index: 0;
                    }
                }
            }
        }

        // 待办列表
        .todo-list {
            width: 100%;
            height: 520px;

            :deep(.y9-list-items) {
                height: 381px;
                overflow: auto;
                scrollbar-width: none !important;

                .y9-list-item {
                    flex-wrap: nowrap;
                    padding: 12px 0px;

                    .y9-list-item-content {
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        color: #666;
                        margin: 0 16px;
                        font-size: 14px;
                        font-weight: 600;
                        cursor: pointer;
                    }

                    .y9-list-item-icon-end {
                        color: #999;
                        font-size: 12px;
                    }

                    .y9-list-item-icon-start {
                        font-size: 14px;
                    }
                }
            }
        }

        // 通知通告
        .info {
            width: 100%;
            margin-bottom: 30px;
            height: 360px;

            :deep(.y9-list) {
                height: 264px;
                overflow: auto;
                scrollbar-width: none !important;

                .y9-list-item {
                    flex-wrap: nowrap;
                    padding: 11px 0px;

                    .y9-list-item-content {
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        color: #666;
                        font-size: 14px;
                        cursor: pointer;
                    }

                    .y9-list-item-icon-end {
                        color: #999;
                        font-size: 12px;
                    }
                }
            }
        }

        // 用户信息
        .user-info {
            width: 70%;
            height: 120px;
            overflow-x: auto;
            scrollbar-width: none !important;
            font-size: 16px;
            font-weight: 600;
            border-radius: 5px 0 0 5px;
            background-color: var(--el-color-primary);
            color: #fff;

            :deep(.el-card__body) {
                display: flex;
                height: 120px;
                padding: 10px;
                // justify-content: center;
                align-items: center;

                .user {
                    display: flex;
                    // justify-content: center;
                    align-items: center;

                    .el-avatar {
                        width: 50px;
                        height: 50px;
                        cursor: pointer;

                        img {
                            background-color: var(--el-color-primary);
                            width: 100%;
                            height: 100%;
                        }
                    }

                    .content {
                        padding: 0 0 0 15px;
                        display: flex;
                        flex-direction: column;

                        .content-top {
                            display: flex;
                            flex-wrap: wrap;
                            align-items: flex-end;

                            .name {
                                font-size: 20px;
                                width: 62px;
                                font-weight: bolder;
                                margin-right: 8px;
                                cursor: pointer;
                            }

                            span {
                                width: 138px;
                                font-weight: 500;
                                font-size: 12px;
                            }
                        }
                    }

                    .content-bottom {
                        margin-top: 20px;
                        width: 100%;
                        font-weight: 500;
                        font-size: 14px;
                        white-space: normal;
                    }
                }
            }
        }

        // 时间
        .time {
            width: 30%;
            height: 120px;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 14px;
            font-weight: 600;
            border-radius: 0 5px 5px 0;
            border-left: none;
            background-color: var(--el-color-primary);
            color: #fff;

            div {
                text-align: center;
            }

            div:nth-child(2) {
                margin: 10px 0 5px 0;
            }
        }

        // 应用列表
        .app-list {
            width: calc(100% - 30px);
            height: 910px;
            position: relative;
            margin-left: 27px;

            :deep(.el-card__body) {
                height: 790px;
                width: 100%;

                .box-list {
                    height: 735px;
                    overflow: auto;
                    scrollbar-width: none !important;
                    display: flex;
                    justify-content: center;
                    // padding: 0 25px;
                    .list-items {
                        display: flex;
                        flex-wrap: wrap;
                        width: 480px;
                        justify-content: space-between;
                        align-content: flex-start;

                        .app-item {
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            margin: 15px 25px;

                            .image {
                                width: 60px;
                                height: 60px;
                                cursor: pointer;

                                img {
                                    width: 100%;
                                    height: 100%;
                                }
                            }

                            span {
                                font-size: 14px;
                                margin-top: 24px;
                                color: rgba(0, 0, 0, 0.85);
                            }
                        }
                    }

                    // .list-items::after {
                    //     content: '';
                    //     flex: 1;
                    // }
                }
            }

            .y9-pagination {
                width: 100% !important;
                justify-content: space-around !important;
                position: absolute;
                bottom: 20px;
                left: 50%;
                transform: translateX(-50%);

                .el-pagination__sizes {
                    margin: 0 0 0 20px;
                }
            }
        }
    }

    :deep(.el-col) {
        padding: 0 !important;
    }

    :deep(.el-card) {
        border-radius: 5px;
    }

    // el-card 阴影效果
    :deep(.el-card.is-always-shadow) {
        box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    }

    // 分页的文字样式
    :deep(.y9-pagination) {
        .el-icon {
            font-size: 14px;
        }

        .total-div {
            font-size: 14px;
        }

        .el-pager {
            li {
                font-size: 14px;
            }
        }

        .el-select {
            .el-input {
                font-size: 14px;
            }
        }
    }

    // card 标题
    .title {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        border-bottom: 1px solid #eee;
        padding-bottom: 15px;

        span {
            font-size: 16px;
            color: #000;
        }

        i {
            font-style: normal;
            font-size: 12px;
            color: #999;
            cursor: pointer;
        }
    }

    //
    :deep(.el-col-8) {
        .card-value:nth-child(1) {
            margin: 0 30px;
        }
    }

    @media screen and (max-width: 1290px) {
        :deep(.op-calendar-pc-table td a) {
            width: 33px;
        }
        :deep(.op-calendar-pc-backtoday) {
            font-size: 12px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 2px;

            .ant-select-selector {
                width: 80px !important;
            }

            .ant-select {
                width: 80px !important;
            }
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 2px;
            width: 80px;

            .ant-select-selector {
                width: 80px !important;
            }

            .ant-select {
                width: 80px !important;
            }
        }
    }

    @media screen and (max-width: 1460px) and (min-width: 1290px) {
        :deep(.op-calendar-pc-table td a) {
            width: 35px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 10px;

            .ant-select-selector {
                width: 85px !important;
            }

            .ant-select {
                width: 85px !important;
            }
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 10px;

            .ant-select-selector {
                width: 85px !important;
            }

            .ant-select {
                width: 85px !important;
            }
        }
    }

    @media screen and (max-width: 1800px) and (min-width: 1460px) {
        :deep(.op-calendar-pc-table td a) {
            width: 42px;
        }
        :deep(.op-calendar-pc-holiday-box) {
            margin-right: 15px;
        }
        :deep(.op-calendar-pc-year-box) {
            margin-right: 15px;
        }
    }

    @media screen and (max-width: 1900px) and (min-width: 1800px) {
        :deep(.op-calendar-pc-table td a) {
            width: 55px;
        }
    }

    @media screen and (min-width: 1900px) {
        :deep(.op-calendar-pc-table td a) {
            width: 58px;
        }
    }
</style>
