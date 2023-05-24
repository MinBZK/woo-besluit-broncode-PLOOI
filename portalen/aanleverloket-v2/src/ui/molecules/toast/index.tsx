import { ToastAtom } from "../../atoms";
import { ToastType } from "../../interfaces/Toast";
import styles from './styles.module.scss';

interface Props {
    message: string;
    type: ToastType;
    onClose?: () => void;
}

export function ToastMolecule(props: Props) {
    return <ToastAtom type={props.type}>
        <div className={styles.toast}>
            <p>{props.message}</p>
        </div>
    </ToastAtom>
}