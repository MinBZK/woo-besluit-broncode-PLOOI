import { fireEvent, render } from '@testing-library/react';
import { FoldableMolecule } from '../../ui/molecules';
import '@testing-library/jest-dom/extend-expect';

const props = {
    label: "label tekst"
};

it('Renders the FoldableMolecule', () => {
   const {getByText} = render(<FoldableMolecule {...props}> <p>test</p></FoldableMolecule>);

   const label = getByText(/label tekst/i)
   expect(label).toBeInTheDocument();
});

it('Renders the child of FoldableMolecule', () => {
   const {getByText} = render(<FoldableMolecule {...props} defaultFolded={false} > <p>child</p> </FoldableMolecule>);

   const label = getByText(/label tekst/i)
   expect(label?.parentElement?.childElementCount).toBe(1);
   expect(label.hasChildNodes()).toBe(true);

   const child = getByText('child');
   expect(child).toBeInTheDocument();
});

it('Renders FoldableMolecule with class container--unfolded', () => {
   const {getByText} = render(<FoldableMolecule {...props} defaultFolded={false} > <p>child</p> </FoldableMolecule>);

   const label = getByText(/label tekst/i)
   expect(label.parentElement?.parentElement).toHaveClass('container--unfolded');
});

it('Renders FoldableMolecule not with class container--unfolded', () => {
   const {getByText} = render(<FoldableMolecule {...props} defaultFolded={true} > <p>child</p> </FoldableMolecule>);

   const label = getByText(/label tekst/i)
   expect(label.parentElement?.parentElement).not.toHaveClass('container--unfolded');
});

it('onClick FoldableMolecule', () => {
   const {getByText} = render(<FoldableMolecule {...props} defaultFolded={true} > <p>child</p> </FoldableMolecule>);

   const label = getByText(/label tekst/i)
   expect(label.parentElement?.parentElement).not.toHaveClass('container--unfolded');

   fireEvent.click(label.parentElement?.parentElement!);
   expect(label.parentElement?.parentElement).toHaveClass('container--unfolded');

});
