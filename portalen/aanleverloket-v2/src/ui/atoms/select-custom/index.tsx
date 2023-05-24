import styles from "./styles.module.scss";

interface Props {
  id: string;
  ariaLabel: string;
  options: string[];
  onChange: () => void;
  selectedValue?: string;
  disabled?: boolean;
}

export function SelectCustomAtom(props: Props) {
  const className = styles.select;

  const options = props.options.map((option) => {
    return <option key={option} value={option}>{option}</option>;
  });

  return (
    <div className={className}>
      <select
        id={props.id}
        name={props.id}
        aria-label={props.ariaLabel}
        disabled={props.disabled}
        value={props.selectedValue}
        onChange={props.onChange}
      >
        {options}
      </select>
    </div>
  );
}
