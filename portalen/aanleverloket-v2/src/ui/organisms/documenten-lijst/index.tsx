import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { unpublish } from "../../../store/reducers/metadata";
import { searchDocuments } from "../../../store/reducers/search";
import { selectSearch } from "../../../store/selectors";
import { formatDate, stringToDate } from "../../../utils/DateFormatter";
import {
  HeadingH2Atom,
  SpacerAtom,
  SpinnerAtom,
  VirtualTableAtom
} from "../../atoms";
import { IconButtonMolecule } from "../../molecules";
import { PopupMolecule } from "../../molecules/popup";

let currPage: number = 0;

export function DocumentenLijstOrganism() {
  const [popup, setPopup] = useState(false);
  const [unpublishItem, setUnpublishItem] = useState("");

  const state = useAppSelector(selectSearch);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const fetchList = useCallback(async () => {
    if (dispatch) {
      dispatch(searchDocuments(currPage));
      currPage += 1;
    }
  }, [dispatch]);

  useEffect(() => {
    fetchList();
  }, [fetchList]);

  useEffect(() => {
    const handleScroll = () => {
      const scrollHeight = window.document.documentElement.scrollHeight;
      const currentHeight = window.document.documentElement.scrollTop + window.innerHeight;
      if (currentHeight + 1 >= scrollHeight) {
        fetchList();
      }
    };
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [currPage, fetchList]);


  const navigateToUpdate = (id: string) => {
    navigate("/update", {
      state: {
        id: id,
      },
    });
  };

  const onUnpublish = (id: string) => {
    dispatch(unpublish(id));
    fetchList(); //------fetching gebeurt nu voordat het op de server veranderd is (status?)
  };

  const extraInfo = () => {
    const item = state.data?._embedded.resultaten.find((f) => f._links.metadata.href.split("/").pop()! === unpublishItem);

    if(item){
      return <div>
        <p>Titel: {item.officieleTitel} <br></br>
        Identifier: {item._links.metadata.href.split("/").pop()!}</p>
      </div>
      // return <TableAtom 
      //   theads={["Title", "Identifier"]}
      //   rows={[
      //     [item.officieleTitel, item._links.metadata.href.split("/").pop()!],
      //   ]}/> 
    }
  };

  const rows = !state.data
    ? []
    : state.data._embedded.resultaten.map((result, index) => [
        `${index + 1}`,
        result.officieleTitel,
        result.documentsoort,
        formatDate(stringToDate(result.openbaarmakingsdatum)),
        formatDate(stringToDate(result.wijzigingsdatum)),
        result.identifiers,
        "Gepubliceerd",
        <IconButtonMolecule key={index} icon={"Zichtbaar"} size={"medium"} id={`${index + 1}+${result.officieleTitel}-zichtbaar`} title="Zichtbaar" onClick={() => {
          setUnpublishItem(result._links.metadata.href.split("/").pop()!);
          setPopup(!popup);
        }} type={"default"} squar/>,
        <IconButtonMolecule key={index + "-2"} icon={"Bewerken"} size={"medium"} id={`${index + 1}+${result.officieleTitel}-bewerken`} title="Bewerken" onClick={() =>
          navigateToUpdate(result._links.metadata.href.split("/").pop()!)} type={"default"} squar/>,
      ]);

  return (
    <>
      <HeadingH2Atom>Documentenlijst</HeadingH2Atom>
      <SpacerAtom space={8} />

      {state.fetching && !state.data ? (
        <SpinnerAtom type={"primary"} />
      ) : (
        <VirtualTableAtom
          theads={[
            "#",
            "Titel",
            "Documentsoort",
            <div><div>Aanlever</div> <div>datum</div> </div>,
            <div><div>Laatst</div> <div>bewerkt</div> </div>,
            "Identifier",
            "Status",
            "",
            "",
          ]}
          rows={rows}
          columnWidth={["4.5%", "25%", "15%", "9.5%", "9.5%", "14%", "11.5%", "5%", "5%"]}
          OnEndOfScroll={fetchList}
        />
      )}
      {popup && unpublishItem ? (
        <PopupMolecule
          title="Depubliceren"
          text="Weet u zeker dat u dit document wil depubliceren?"
          cancelText="ANNULEER"
          okeText="DEPUBLICEER"
          cancelButton={() => {
            setPopup(!popup);
            setUnpublishItem("");
          }}
          okeButton={() => {
            setPopup(!popup);
            onUnpublish(unpublishItem);
            setUnpublishItem("");
          }}
          extraInfo={extraInfo()}
        />
      ) : null}
    </>
  );
}
