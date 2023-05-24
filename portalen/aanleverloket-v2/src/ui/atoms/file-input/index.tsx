import { ChangeEvent } from "react";
import styles from "./styles.module.scss";

interface Props {
  children: any;
  onFileChanged: (file?: File) => any;
  accept?: string;
  inputRef?: any;
}

export function FileInputAtom(props: Props) {
  let element: HTMLInputElement | null;

  const _handleClick = () => {
    if (element == null) return;

    element.click();
  };

  const _onFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    props.onFileChanged(event.target?.files ? event.target.files[0] : undefined);
  };

  return <div className={styles.file_input__container} onClick={_handleClick}>
    <input role={"input"} accept={props.accept} className={styles.file_input}
      onClick={() =>  props.onFileChanged()}
      onChange={_onFileChange} ref={r => {
        element = r;
        props.inputRef && props.inputRef({...r, focus: _handleClick});
      }} type='file' />
    {
      props.children
    }
  </div>
}
