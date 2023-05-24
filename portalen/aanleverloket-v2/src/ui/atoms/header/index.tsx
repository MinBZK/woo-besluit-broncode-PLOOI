import styles from "./styles.module.scss";

interface Props {
    children?: any;
}

export function HeaderAtom(props: Props) {
    return <header className={styles.header}>
        {
            props.children
        }
    </header>
}