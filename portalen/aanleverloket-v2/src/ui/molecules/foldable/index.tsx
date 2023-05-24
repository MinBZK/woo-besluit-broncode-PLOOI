import { useState } from "react";
import { DividerAtom } from "../../atoms";
import { LabelMolecule } from "../label";
import styles from "./styles.module.scss";

interface Props {
  label: string;
  children: any;
  defaultFolded?: boolean;
}

export function FoldableMolecule(props: Props) {
  const [folded, setFolded] = useState(props.defaultFolded);

  let className = styles.container;
  if (!folded) className += ` ${styles["container--unfolded"]}`;

  return (
    <>
      <div onClick={() => setFolded(!folded)} className={className}>
        <LabelMolecule label={props.label} />
      </div>
      <DividerAtom verticaal />
      {!folded && props.children}
    </>
  );
}
