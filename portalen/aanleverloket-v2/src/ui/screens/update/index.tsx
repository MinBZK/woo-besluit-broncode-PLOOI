import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { fetchForm } from "../../../store/reducers/metadata";
import { selectMetadata } from "../../../store/selectors";
import { SpinnerAtom } from "../../atoms";
import { SmallLayout } from "../../layouts";
import {
  OverheidHeaderOrganism,
  ToastOrganism,
  UpdateFormulierOrganism,
} from "../../organisms";


export default function UpdateScreen() {
  const state = useAppSelector(selectMetadata);
  const dispatch = useAppDispatch();
  const location = useLocation();
  const {id} = location.state as any || {};

  useEffect(() => {
    if(id)
      dispatch(fetchForm(id));
  }, []);

  return (
    <SmallLayout
      header={<>
        <ToastOrganism />
        <OverheidHeaderOrganism />
      </>}
      body={
          (state.fetching && <SpinnerAtom type={"primary"}/>)
          || ((!id || (state.data && id != state.data[0].meta.document.pid?.split("/").pop()!)) && <div>Error fetching data.</div>) ///>>> moet state clearen na gebruik, vanplaats checken of de huidige staat de zelfde pid heeft
          || (state.data && <UpdateFormulierOrganism data={state.data}/>)
          || (<div>Error fetching data!</div>)
      }
    />
  );
}
