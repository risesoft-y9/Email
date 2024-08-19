/*
 * @Author: your name
 * @Date: 2022-01-10 18:09:52
 * @LastEditTime: 2024-04-10 16:55:59
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-email/src/main.js
 */
import router from '@/router/index';
import { setupStore } from '@/store';
import 'animate.css';
import 'normalize.css'; // 样式初始化
import 'remixicon/fonts/remixicon.css';
import { createApp } from 'vue';
import sso from 'y9plugin-sso';
import y9pluginComponents from 'y9plugin-components';
import App from './App.vue';
import './theme/global.scss';
import i18n from './language';

import customDirective from '@/utils/directive';

// 传入sso所需的环境变量
const env = {
    sso: {
        VUE_APP_SSO_DOMAINURL: import.meta.env.VUE_APP_SSO_DOMAINURL, // sso接口
        VUE_APP_SSO_CONTEXT: import.meta.env.VUE_APP_SSO_CONTEXT, // sso接口上下文
        VUE_APP_SSO_AUTHORIZE_URL: import.meta.env.VUE_APP_SSO_AUTHORIZE_URL, //sso授权码接口
        VUE_APP_SSO_LOGOUT_URL: import.meta.env.VUE_APP_SSO_LOGOUT_URL, //退出URL
        VUE_APP_SSO_CLIENT_ID: import.meta.env.VUE_APP_SSO_CLIENT_ID, //sso接口的固定字段
        VUE_APP_SSO_SECRET: import.meta.env.VUE_APP_SSO_SECRET, //sso接口的固定字段
        VUE_APP_SSO_GRANT_TYPE: import.meta.env.VUE_APP_SSO_GRANT_TYPE, //sso接口的固定字段
        VUE_APP_SSO_SITETOKEN_KEY: import.meta.env.VUE_APP_SSO_SITETOKEN_KEY //sso-token_key
        // VUE_APP_REDISKEY: import.meta.env.VUE_APP_REDISKEY, //sso-redisKey
        // VUE_APP_SESSIONSTORAGE_GUID: import.meta.env.VUE_APP_SESSIONSTORAGE_GUID, //sso-sessionStorage_guid
        // VUE_APP_SERVER_REDIS: import.meta.env.VUE_APP_SERVER_REDIS //sso-redisServerUrl
    },
    logInfo: {
        showLog: true
    }
};

const app: any = createApp(App);
app.use(sso, { env });

setupStore(app);
app.use(router);
app.use(customDirective);
app.use(y9pluginComponents);
app.use(i18n);
app.mount('#app');

export const $y9_SSO = app.$y9_SSO;
