import { ILink } from "../../interfaces/Link";
import styles from "./styles.module.scss";

export function LinkAtom(props: ILink) {
    let className = styles.link;
    if(props.selected) className += ` ${styles.link__selected}`

    const target = props.newWindow ? "_blank" : undefined;

    return <a target={target} className={className} href={props.href} lang={props.lang} >{props.text}</a>
}