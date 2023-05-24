import { useEffect, useState } from "react";

// Should reference breakpoints.scss
// 1em = 16px
const desktopBreakpoint = 16 * 50;

interface HiddenProps extends Props {
    target: "desktop" | "mobile";
}

interface Props {
    children?: any;
}

function Hidden(props: HiddenProps) {
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);
    const handleResize = () => setWindowWidth(window.innerWidth);

    useEffect(() => {
        window.addEventListener('resize', handleResize)
    }, []);

    if (props.target === 'desktop') {
        return windowWidth < desktopBreakpoint ? props.children : <></>;
    } else {
        return windowWidth >= desktopBreakpoint ? props.children : <></>;
    }
}

export function MobileHiddenAtom(props: Props) {
    return <Hidden target="mobile">{
        props.children
    }</Hidden>
}

export function DesktopHiddenAtom(props: Props) {
    return <Hidden target="desktop">{
        props.children
    }</Hidden>
}