import { Cookies } from 'react-cookie';

const cookie = new Cookies();

export const setCookie = (name, value, options) => {
    return cookie.set(name, value, { ...options });
}

export const getCookie = (name) => {
    console.log('🍪 Getting cookie:', cookie);
    return cookie.get(name);
}