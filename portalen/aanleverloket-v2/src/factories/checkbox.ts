import { Identifier } from "../models";
import { ICheckbox } from "../ui/interfaces/Checkbox";

export class CheckboxFactory {
  public create = (
    waardelijstItem: Identifier,
    checked: boolean,
    onClick: () => void,
    bold?: boolean,
    disabled?: boolean
  ): ICheckbox => ({
    id: waardelijstItem.id,
    label: waardelijstItem.label ?? waardelijstItem.id,
    checked: checked,
    onClick: onClick,
    bold: bold,
    disabled: disabled,
  });
}
