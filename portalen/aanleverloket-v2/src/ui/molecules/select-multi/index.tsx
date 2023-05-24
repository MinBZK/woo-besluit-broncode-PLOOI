import _ from "lodash";
import { useEffect, useRef, useState } from "react";
import { IconLabelMolecule } from "..";
import { IListValidator } from "../../../models/validator";
import { ChipAtom, SpacerAtom, TextInputAtom } from "../../atoms";
import { ICheckbox } from "../../interfaces/Checkbox";
import { CheckboxListMolecule } from "../checkbox-list";
import { LabelMolecule } from "../label";
import styles from "./styles.module.scss";

export interface CategoryList {
  title?: string;
  options: ICheckbox[];
  sublist?: CategoryList[];
}

interface Props {
  id: string;
  label: string;
  placeholder?: string;
  categories: CategoryList[];
  tooltip?: string | string[];
  required?: boolean;
  disabled?: boolean;
  singleSelect?: boolean;
  search?: boolean;
  validations?: IListValidator[];
  inputRef?: (ref: HTMLInputElement) => void;
}

export function SelectMultiMolecule(props: Props) {
  const [showValidations, setShowValidationMessage] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const containerRef = useRef<HTMLElement>();
  const inputRef = useRef<HTMLInputElement>();

  useEffect(() => {
    document.addEventListener("mousedown", detectClickArea);
    return () => {
      document.removeEventListener("mousedown", detectClickArea);
    };
  }, [expanded]);

  let classNameInput = styles.select;
  let classNameResults = styles.results;
  let classNameControls = styles.controls;

  const filterBySearch = (lists: CategoryList[], searchTerm: string): CategoryList[] => {
    if (!searchTerm || searchTerm.length === 0) return lists;

    const clone = _.cloneDeep(lists);
    const lowerSearch = searchTerm.toLowerCase();
    const filteredLists = clone.map((list) => { return filter(list, lowerSearch); });

    if (filteredLists.filter(f => f.sublist && f.sublist.length > 0).length > 0)
      return filteredLists.filter(f => f.sublist && f.sublist.length > 0);

    return filteredLists.filter((list) => list.options.length > 0);
  };

  const filter = function (list: CategoryList, search: string) {
    if (!list) return list;

    if (list.title && list.title.toLocaleLowerCase().indexOf(search) > -1) { return list; }

    list.options = list.options.filter((o) => o.label.toLocaleLowerCase().indexOf(search) > -1);
    if (list.sublist && list.sublist.length > 0) {
      for (let j = list.sublist.length - 1; j >= 0; j--)
        filter(list.sublist[j], search);

      list.sublist = list.sublist.filter(f => f.options.length > 0);
    }

    return list;
  }

  const chips = function (items: CategoryList[]): ICheckbox[] | undefined {
    let checked;
    let sublistChecks;

    for (let i = 0; i < items.length; i++) {
      if (items[i].options && items[i].options.filter(f => f.checked)?.length > 0) {
        let c = items[i].options.filter(f => f.checked);
        if (c && c.length > 0) {
          if (checked)
            checked = [...checked, ...c]
          else
            checked = c;
        }
      }

      if (items[i].sublist && items[i].sublist!.length > 0) {
        let s = chips(items[i].sublist!);
        if (s && s.length > 0) {
          if (sublistChecks)
            sublistChecks = [...sublistChecks, ...s]
          else
            sublistChecks = s;
        }
      }
    }

    if (!checked && !sublistChecks) {
      return undefined;
    } else if (checked && !sublistChecks) {
      return checked;
    } else if (!checked && sublistChecks) {
      return sublistChecks;
    } else if (checked && sublistChecks) {
      return checked.concat(sublistChecks);
    }
  }

  if (chips.length > 0) {
    classNameControls += ` ${styles[`controls--selected`]}`;
  }

  if (expanded) {
    classNameInput += ` ${styles[`select--expand`]}`;
    classNameResults += ` ${styles[`results--expand`]}`;
  }

  const filteredOptions = filterBySearch(props.categories, searchTerm);

  const detectClickArea = (event: any) => {
    if (containerRef.current && !containerRef.current.contains(event.target)) {
      setSearchTerm("");

      if (expanded)
        onFocusLost();

      setExpanded(false);

      return;
    }

    setExpanded(true);
    inputRef.current?.focus();
  };

  const onFocusLost = () => {
    expanded && inputRef.current?.focus()
    // setExpanded(false)

    //  if (props.required)
    setShowValidationMessage(true);
    //   else
    //     setShowValidationMessage(false);
  };

  const getSublistsOptions = function (item: CategoryList) {
    const options = item.options.filter(o1 => !item.sublist?.some(o2 => o1.label === o2.title));
    const sublist: any = item.sublist?.filter(f => f.sublist).map(t => getSublistsOptions(t));

    return <div className={styles.sub}><CheckboxListMolecule title={item.title} options={options} key={item.title}>{sublist}</CheckboxListMolecule><SpacerAtom space={2} /></div>;
  }

  const validationErrors = props.validations?.map(v => v.validate(chips(props.categories) ?? [])).filter(err => err !== undefined);

  if (showValidations && (validationErrors?.length ?? 0) > 0) {
    classNameInput += ` ${styles[`select--alert`]}`;
    classNameResults += ` ${styles[`results--alert`]}`;
  }
  return (
    <div className={styles.container}>
      <LabelMolecule
        id={props.id}
        label={props.label}
        tooltip={props.tooltip}
        required={props.required}
      />
      <div
        className={classNameInput}
        ref={(r) => {
          if (r) {
            containerRef.current = r;

            if (props.search == false && props.singleSelect && props.inputRef) {
              const newRef = { ...r, onclick: () => setExpanded(true), focus: () => setExpanded(true), blur: onFocusLost }
              props.inputRef(newRef as any);
            }
          }
        }}
      >
        <div id={props.id} key={"input" + props.label} className={classNameControls}>
          {chips(props.categories)?.map((c, index) => props.singleSelect ? <div className={styles.singleSelect} >{c.label}</div> : <ChipAtom key={c.id + "-" + index} text={c.label} onRemove={c.onClick} />)}

          {(props.search == undefined || props.search) &&
            <TextInputAtom
              disabled={props.disabled}
              // id={props.id}
              inputRef={(r) => {
                props.inputRef && props.inputRef(r);
                inputRef.current = r;
              }}
              onEscape={() => {
                setExpanded(false)
              }}
              onFocus={(e) => {
                e.stopPropagation();
                setExpanded(true);
              }}
              type={"text"}
              value={searchTerm}
              placeholder={chips(props.categories) && chips(props.categories)!.length > 0 ? undefined : props.placeholder}
              onChange={setSearchTerm}
              onBlur={onFocusLost}
            />
          }
          {props.search == false && !(chips(props.categories) && chips(props.categories)!.length > 0) &&
            <p className={styles.nosearch}>{props.placeholder}</p>
            // <p id={props.id} className={styles.nosearch}>{props.placeholder}</p>
          }
        </div>

        <div key={"options" + props.label} className={classNameResults}>
          {filteredOptions && filteredOptions.length > 0
            ? filteredOptions.map((r, index) => <>
              {
                index !== 0 &&
                <SpacerAtom space={4} />
              }
              {
                !r.sublist && <CheckboxListMolecule {...r} key={index} />
              }
              {
                r.sublist && getSublistsOptions(r)
              }
            </>)
            : "Geen resultaten"}
        </div>

      </div>
      {
        validationErrors && validationErrors.length > 0 && showValidations &&
        <IconLabelMolecule icon={"icon-alert-red"} label={validationErrors[0] as string} type={"alert"} />
      }
    </div>
  );
}
