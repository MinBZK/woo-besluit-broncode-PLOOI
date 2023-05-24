import styles from './styles.module.scss';

interface Props {
    children: string | string[];
    for?: string;
    type: "default" | "blue" | "primary" | "orange" | "success" | "warning" | "danger" | "large" | "form";

    bold?: boolean;
    underlined?: boolean;
    italic?: boolean;
}

export function LabelAtom(props: Props) {
    let className = `${styles.label} ${styles[`label--${props.type}`]}`;

    if (props.bold)
        className += ` ${styles[`label--bold`]}`;

    if (props.underlined)
        className += ` ${styles[`label--underlined`]}`;

    if (props.italic)
        className += ` ${styles[`label--italic`]}`;

    return <label htmlFor={props.for} className={className}>{
        Array.isArray(props.children) ? props.children.map((c) => <p>{c}</p>) : props.children
    }</label>
}

