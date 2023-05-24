import {
  CheckboxAtom,
  LabelAtom,
  ListAtom,
  ListItemAtom,
  SpacerAtom,
} from "../../atoms";
import { ICheckbox } from "../../interfaces/Checkbox";
import styles from "./styles.module.scss";

interface Props {
  title?: string;
  options: ICheckbox[];
  children?: any;
}

interface SelectAllProps extends Props {
  onSelectAll: () => void;
  selectAllLabel: string;
}

export function CheckboxListMolecule(props: Props) {
  const options = props.options.map((option) => {
    return (
      <ListItemAtom key={option.id} bold={option.bold}>
        <CheckboxAtom {...option} />
      </ListItemAtom>
    );
  });

  return (
    <div className={styles.list}>
      {props.title && (
        <>
          <LabelAtom type={"form"} bold>
            {props.title}
          </LabelAtom>
          <SpacerAtom space={2} />
        </>
      )}

      <ListAtom unstyled>{options}{props.children}</ListAtom>
    </div>
  );
}

export function CheckboxListSelectAllMolecule(props: SelectAllProps) {
  const selectAllOption: ICheckbox = {
    id: "select_all",
    checked: props.options.every((o) => o.checked),
    onClick: props.onSelectAll,
    label: props.selectAllLabel,
    bold: true,
  };

  return (
    <div className={styles.selectAll}>     
      <CheckboxListMolecule
        options={[selectAllOption, ...props.options]}
        title={props.title}
      />
    </div>

  );
}
