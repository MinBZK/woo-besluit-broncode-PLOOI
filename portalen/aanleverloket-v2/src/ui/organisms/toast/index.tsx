import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { clearToast } from "../../../store/reducers/toast";
import { selectToast } from "../../../store/selectors";
import { ToastMolecule } from "../../molecules";

export function ToastOrganism() {
    const { message, type, autoClose } = useAppSelector(selectToast)
    const dispatch = useAppDispatch();

    if (type && message)
        return <ToastMolecule
            onClose={autoClose ? () => dispatch(clearToast()) : undefined}
            // title={message.title} 
            message={message.message} type={type} />

    return <></>
}