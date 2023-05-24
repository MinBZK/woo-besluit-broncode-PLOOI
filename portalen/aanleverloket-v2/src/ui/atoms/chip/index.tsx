import styles from "./styles.module.scss";

interface Props {
  text: string;
  onRemove: () => void;
}

export function ChipAtom(props: Props) {
  return (
    <div className={styles.chip}>
      {props.text}
      <button
        id={"remove-" + props.text}
        className={styles.chip__button}
        onClick={props.onRemove}
        name={"remove-" + props.text}
        type="button" 
      >x</button>
    </div>
  );
}
