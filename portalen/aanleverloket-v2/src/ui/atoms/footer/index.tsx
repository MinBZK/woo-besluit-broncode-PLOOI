import styles from './styles.module.scss';

interface Props {
    children?: any;
}

export function FooterAtom(props: Props) {
    return <footer
        className={styles.footer}
        role="contentinfo">
        {
            props.children
        }
    </footer>
}