export interface ICheckbox {
  id: string;
  checked: boolean;
  disabled?: boolean;
  bold?: boolean;
  onClick: () => void;
  label: string;
}
