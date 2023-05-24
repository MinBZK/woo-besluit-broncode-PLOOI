import styles from './styles.module.scss';

interface Props {
    type: 'primary' | 'white'
    size?: 'small' | 'large'
}

export function SpinnerAtom(props: Props) {

    let className = `${styles[`spinner__${props.type}`]}`;
    if(props.size) className += ` ${styles[`spinner__${props.size}`]}`;
    else className += ` ${styles[`spinner__small`]}`;

    return <div data-testid={"spinner"} className={styles.spinner}><div className={className} /></div>

}