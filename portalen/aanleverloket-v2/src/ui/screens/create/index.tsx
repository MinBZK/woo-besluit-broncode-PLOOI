import { useEffect, useMemo } from "react";
import { useAppDispatch } from "../../../store/hooks";
import { addForm, clear } from "../../../store/reducers/metadata";
import { SidebarLayout } from "../../layouts";
import {
  OverheidHeaderOrganism,
  ToastOrganism,
  CreateFormulierOrganism,
  SidebarOrganism,
} from "../../organisms";

export default function CreateScreen() {
  const dispatch = useAppDispatch();

  useMemo(() => {    
    dispatch(clear());
    dispatch(addForm());
  }, []);

  return (
    <SidebarLayout
      sidebar={<SidebarOrganism />}
      header={
        <>
          <ToastOrganism />
          <OverheidHeaderOrganism />
        </>
      }
      body={<CreateFormulierOrganism />}
    />
  );
}
