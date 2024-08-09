/*
 * @Author: fanzhengyang
 */

const authRouter = [
    {
        path: '/auth',
        redirect: '/receive',
        name: 'authOrg',
        hidden: true,
        meta: {
            title: '普通用户',
            icon: 'el-icon-s-custom',
            roles: ['user'],
            notShowAdmin: true
        }
    }
];

export default authRouter;
