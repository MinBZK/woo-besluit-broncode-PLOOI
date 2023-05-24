import { ContainerAtom } from '../../atoms';
import styles from '../layouts.module.scss';

interface Props {
    header: JSX.Element;
    body: JSX.Element;
    footer?: JSX.Element;
}

export function DefaultLayout(props: Props) {
    let className = styles.layouts__page__body;
    if(!props.footer) className += ` ${styles.layouts__page__padding}`

    return <div className={styles.layouts__page}>
        {
            props.header
        }

        <span className={className}>
            <ContainerAtom centered type='row'>
                {
                    props.body
                }
            </ContainerAtom>
        </span>

        {
            props.footer
        }
    </div>
}