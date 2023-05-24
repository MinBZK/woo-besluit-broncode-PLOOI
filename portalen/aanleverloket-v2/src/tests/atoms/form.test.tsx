import { fireEvent, render, screen } from '@testing-library/react';
import { FormAtom } from '../../ui/atoms';
import '@testing-library/jest-dom/extend-expect';


it('Renders the FormAtom', async () => {
   const {getByText} = render(<FormAtom><p>leeg formulier</p></FormAtom>);

   const text = getByText(/leeg formulier/i);
   expect(text.parentNode).toBeInTheDocument();
});

it('Has the class form', async () => {
   const {getByText} = render(<FormAtom><p>leeg formulier</p></FormAtom>);

   const text = getByText(/leeg formulier/i);
   expect(text.parentElement).toHaveClass("form");
});

it('Renders the FormAtom child', async () => {
   const {getByText} = render(<FormAtom><p>leeg formulier</p></FormAtom>);

   const text = getByText(/leeg formulier/i);
   expect(text.parentElement?.childElementCount).toBe(1);
   expect(text.hasChildNodes()).toBe(true);
});
