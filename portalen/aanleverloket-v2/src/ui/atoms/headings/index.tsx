import styles from './styles.module.scss';

interface Props { children: any; }

export function HeadingH1Atom(props: Props) {
    const className = `${styles.heading} ${styles.heading__h1}`;
    return <h1 className={className}>{props.children}</h1>
}

export function HeadingH2Atom(props: Props) {
    const className = `${styles.heading} ${styles.heading__h2}`;
    return <h2 className={className}>{props.children}</h2>
}

export function HeadingH3Atom(props: Props) {
    const className = `${styles.heading} ${styles.heading__h3}`;
    return <h3 className={className}>{props.children}</h3>
}

export function HeadingH4Atom(props: Props) {
    const className = `${styles.heading} ${styles.heading__h4}`;
    return <h4 className={className}>{props.children}</h4>
}

export function HeadingPreviewAtom(props: Props) {
    const className = `${styles.heading} ${styles.heading__preview}`;
    return <p className={className}>{props.children}</p>
}  