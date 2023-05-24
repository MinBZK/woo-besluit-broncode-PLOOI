import styles from './styles.module.scss';

interface Props {
    space: 1 | 2 | 4 | 8 | 16 | "58px"
}

export function SpacerAtom(props:Props){
   return <div data-testid={"spacer"} className={styles[`spacer__${props.space}`]} />
}