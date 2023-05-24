import styles from './styles.module.scss';

interface Props {
    children: any;
    onMouseEnter: () => void;
    onMouseLeave: () => void;
}

export function TooltipAtom(props: Props) {
    return <div onMouseEnter={props.onMouseEnter} onMouseLeave={props.onMouseLeave} className={styles.tooltip}>
        {
            props.children
        }
    </div>

}