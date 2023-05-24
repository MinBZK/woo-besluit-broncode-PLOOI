import { ChangeEvent } from 'react';
import styles from './styles.module.scss';

interface Props {
    placeholder: string;
    rows?: number;
    readonly?: boolean;
    id?: string;
    value: string;

    onChange?: (text: string) => void;
}

export function TextAreaAtom(props: Props) {
    const handleChange = (ev: ChangeEvent<HTMLTextAreaElement>) => {
        if (!props.onChange)
            return;
            
        props.onChange(ev.currentTarget.value);
    };

    return <textarea value={props.value} onChange={handleChange} id={props.id} readOnly={props.readonly} className={styles.textarea} placeholder={props.placeholder} rows={props.rows ?? 5} />
}