import styles from './styles.module.scss';

interface Props
{
    text: string;
}

export function SmallAtom(props: Props)
{
    return <small className={styles.small}>{props.text}</small>
}