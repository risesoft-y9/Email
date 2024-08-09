import { $validCheck } from '@/utils/validate';

const directiveObj = {
    /**
     * elInputCheck指令使用说明：
     *        v-elInputCheck="{required:true,message:'请输入移动电话',regularType:'phone',regularValue:personForm.mobile,regularMessage:"xxxx"}"
     *            required:是否必填，可选，不传入则默认为true
     *            message:必填的校验信息。required=true时，必须传入
     *        regularType:正则校验规则，可选
     *        regularValue:正则校验的值，注意，regularType存在值，该变量必须传入。
     *        regularMessage:正则校验错误信息，可选，不传入则使用默认的正则信息提示。
     *
     */
    elInputCheck: {
        mounted(el, binding, vnode, prevnode) {
            const input = el.getElementsByTagName('input'); //获取input元素

            const { required, message, regularType, regularMessage, regularValue } = binding.value; //解构传入的值

            let RegularMsg = ''; //正则校验错误信息

            if (regularValue && regularType && (required === false ? regularValue : true)) {
                const checkObj = $validCheck(regularType, regularValue, true);
                if (!checkObj.valid) {
                    RegularMsg = regularMessage ? regularMessage : checkObj.msg;
                    input[0].elInputCheckResult = false;
                } else {
                    RegularMsg = '';
                    input[0].elInputCheckResult = true;
                }
            }

            input[0].addEventListener('blur', (e) => {
                const spanTag = el.getElementsByTagName('span');

                RegularMsg = '';

                if (regularType && (required === false ? e.target.value : true)) {
                    const checkObj = $validCheck(regularType, e.target.value, true);
                    if (!checkObj.valid) {
                        RegularMsg = regularMessage ? regularMessage : checkObj.msg;
                    }
                }
                const requiredCondition = (required || required == undefined) && !e.target.value; //是否为必填的条件，不传入required，默认为必填

                if (requiredCondition || RegularMsg) {
                    //如果为必填，或者正则校验不通过

                    e.target.style.borderColor = 'red'; //设置input元素边框为红色

                    //创建span标签来显示校验不通过的message
                    if (spanTag.length === 0) {
                        const span = document.createElement('span'); //创建span标签
                        span.className = 'custom-validator-text'; //给span标签添加class样式
                        el.appendChild(span); //把span标签加入到el中
                    }

                    spanTag[0].innerHTML = requiredCondition ? message : RegularMsg; //显示错误信息在span标签中

                    e.target.elInputCheckResult = false;

                    if (el.className.indexOf('flex-direction-column') === -1) {
                        el.className += ' ' + 'flex-direction-column';
                    }

                    spanTag[0].style.display = 'block'; //显示
                } else {
                    e.target.style.borderColor = 'rgb(0 0 0 / 6%)'; //恢复正常的边框颜色

                    if (spanTag.length > 0) {
                        //如果有span标签则隐藏
                        spanTag[0].style.display = 'none'; //隐藏
                    }

                    e.target.elInputCheckResult = true;
                }
            });
        }
    }
};

export default (createApp) => {
    for (const _key in directiveObj) {
        createApp.directive(_key, directiveObj[_key]);
    }
};
