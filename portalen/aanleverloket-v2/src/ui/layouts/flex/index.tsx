import { ContainerAtom } from '../../atoms';
import styles from '../layouts.module.scss';

interface Props {
    header: JSX.Element;
    body: JSX.Element;
    footer?: JSX.Element;
}

export function FlexLayout(props: Props) {
    let className = styles.layouts__page__body + " " + styles.layouts__page__body__overflow_auto;
    if(!props.footer) className += ` ${styles.layouts__page__padding}`

    return <div className={styles.layouts__page + " " + styles.layouts__page__overflow_hidden}>
        {
            props.header
        }

        <span className={className}>
            <ContainerAtom centered type='flex'>
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