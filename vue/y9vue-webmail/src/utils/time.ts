export function changeTime() {
    const list = [];
    const time = new Date();
    const year = time.getFullYear();
    let month: any = time.getMonth() + 1;
    let day: any = time.getDate();
    let hour: any = time.getHours();
    let minutes: any = time.getMinutes();
    let seconds: any = time.getSeconds();
    if (month < 10) {
        month = `0${month}`;
    }
    if (day < 10) {
        day = `0${day}`;
    }
    if (hour < 10) {
        hour = ` 0${hour}`;
    } else {
        hour = `${hour}`;
    }
    if (minutes < 10) {
        minutes = `0${minutes}`;
    }
    if (seconds < 10) {
        seconds = `0${seconds}`;
    }
    // 年份
    const yearValue = `${year}-${month}-${day}`;
    // 时间
    const timeValue = `${hour}:${minutes}`;
    // 星期
    const weeks = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    list.splice(0, 0, yearValue, weeks[time.getDay()], timeValue);
    return list;
}
