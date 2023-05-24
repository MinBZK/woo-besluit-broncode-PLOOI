import { render } from '@testing-library/react';
import { ToastMolecule } from '../../ui/molecules';
import '@testing-library/jest-dom/extend-expect';

const props = {
    // title: "title",
    message: "message",
    onClose: () => {},
};

it('Renders the ToastMolecule', () => {
   const {getByText} = render(<ToastMolecule {...props} type={"info"}/>);

   const title = getByText(/message/i)
   expect(title.parentElement?.parentElement).toBeInTheDocument();
});