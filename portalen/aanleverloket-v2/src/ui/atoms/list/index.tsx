import styles from './styles.module.scss';

interface Props {
    children?: any;
    unstyled?:boolean;
    linked?:boolean
}

export function ListAtom(props: Props) {
    let className = `${styles.list}`;

    if (props.linked)
        className += ` ${styles["list--linked"]}`;

    if (props.unstyled)
        className += ` ${styles["list--unstyled"]}`;

    return <ul className={className}>
        {
            props.children
        }
    </ul>
}