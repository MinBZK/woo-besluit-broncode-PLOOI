import { ContainerAtom, MobileHiddenAtom } from '../../atoms';
import styles from '../layouts.module.scss';

interface Props {
    header: JSX.Element;
    sidebar: JSX.Element;
    body: JSX.Element;
    footer?: JSX.Element;
}

export function SidebarLayout(props: Props) {
    let className = styles.layouts__page__body;
    if(!props.footer) className += ` ${styles.layouts__page__padding}`

    return <div className={styles.layouts__page}>        
        {
            props.header
        }

        <span className={className}>
            <ContainerAtom centered type='row'>
                <MobileHiddenAtom>
                    <div className={styles.layouts__page__sidebar}>
                        {
                            props.sidebar
                        }
                    </div>
                </MobileHiddenAtom>
                <ContainerAtom>
                    {
                        props.body
                    }
                </ContainerAtom>
            </ContainerAtom>
        </span>

        {
            props.footer
        }
    </div>
}