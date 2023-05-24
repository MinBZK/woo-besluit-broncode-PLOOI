import styles from '../layouts.module.scss';

interface Props {
    children: any;
}

export function CenteredLayout(props: Props) {
    return <div className={styles.layouts__page}>
        <div className={styles.layouts__centered__body}>
            {
                props.children
            }
        </div>
    </div>
}