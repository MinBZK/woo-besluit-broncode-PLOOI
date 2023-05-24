import { FormAtom } from '../../atoms';
import styles from './styles.module.scss';

interface Props {
    children: any;
    rightButton?: JSX.Element;
    leftButton?: JSX.Element;
}

export function FormMolecule(props: Props) {
    return <FormAtom additionalClassname={styles.form}>
        {
            props.children
        }
        {
            (props.rightButton || props.leftButton) &&
            <div className={styles.form__button_container}>
                {
                    props.leftButton
                }
                <div className={styles.form__button_container__spacer} />
                {
                    props.rightButton
                }
            </div>
        }
    </FormAtom>
}