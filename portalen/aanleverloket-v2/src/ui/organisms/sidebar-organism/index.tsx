import { useAppSelector, useAppDispatch } from "../../../store/hooks";
import { removeForm, addForm, changeFormIndex, uploadForm } from "../../../store/reducers/metadata";
import { selectMetadata } from "../../../store/selectors";
import { ContainerAtom, SpacerAtom, TableAtom } from "../../atoms";
import { IconButtonMolecule, ButtonMolecule } from "../../molecules";

export function SidebarOrganism() {
    const state = useAppSelector(selectMetadata);
    const dispatch = useAppDispatch();

    const rows = state.data.map((result, index) => [
        <div onClick={()=>{dispatch(changeFormIndex(index));}}> {`${index === 0 ? "" : "\xa0\xa0\xa0\xa0\xa0"} ${result.meta.document.titelcollectie.officieleTitel}`} </div>,
        index === 0 ? (
            <></>
        ) : (
            <IconButtonMolecule
                key={index}
                icon={"icon-remove-blue"}
                size={"medium"}
                id={`${index + 1}-${result.meta.document.titelcollectie.officieleTitel
                    }-remove`}
                title={"verwijder bijlagen"}
                onClick={() => {
                    removeAttachment(index);
                }}
                type={"default"}
                squar
            />
        ),
    ]);

    const removeAttachment = (index: number) => {
        dispatch(removeForm(index))
    };

    const addAttachment = () => {
        dispatch(addForm());
    }

    const onUpload = () => {
        dispatch(uploadForm());
    }

    return <ContainerAtom type="flex">
        <SpacerAtom space={16} />
        <TableAtom
            theads={["Gerelateerde documenten", ""]}
            rows={rows}
            columnWidth={["82%", "auto"]}
            highlight={state.formIndex}
        />
        <SpacerAtom space={4} />
        <div>
            <ButtonMolecule
                text={"Document(en) publiceren"}
                id={"Document(en)-publiceren"}
                title={"Document(en) publiceren"}
                onClick={ onUpload }
                type={"primary"}
            />
            <SpacerAtom space={4} />
            <IconButtonMolecule
                text={"Voeg bijlage toe"}
                id={"Voeg-bijlage-toe"}
                title={"Voeg bijlage toe"}
                onClick={addAttachment}
                type={"default"}
                icon={"icon-plus-shape"}
            />
        </div>
    </ContainerAtom>
}