
export const stringToDate = (value: string) => {
    const pattern = /(\d{2})\.(\d{2})\.(\d{4})/;
    return new Date(value.replace(pattern, '$3-$2-$1'));
}

export const padTo2Digits = (num: number) => {
    return num.toString().padStart(2, '0');
}

export const formatDate = (date = new Date()) => {
    return [
        padTo2Digits(date.getFullYear()),
        padTo2Digits(date.getMonth() + 1),
        date.getDate() < 10 ? `0${date.getDate()}` : date.getDate(),
    ].join('-');
}