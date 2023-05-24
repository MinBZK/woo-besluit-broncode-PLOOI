import { render } from '@testing-library/react';
import { CenteredLayout } from '../../ui/layouts';
import '@testing-library/jest-dom/extend-expect';


it('Renders the CenteredLayout', () => {
   const {getByText} = render(<CenteredLayout><p>child</p></CenteredLayout>);

   const child = getByText(/child/i)
   expect(child.parentElement?.parentElement).toBeInTheDocument();
});

it('Has the class layouts__page', () => {
    const {getByText} = render(<CenteredLayout><p>child</p></CenteredLayout>);

    const child = getByText(/child/i)
    expect(child.parentElement?.parentElement).toHaveClass('layouts__page');
});

it('Has the class layouts__centered__body', () => {
    const {getByText} = render(<CenteredLayout><p>child</p></CenteredLayout>);

    const child = getByText(/child/i)
   expect(child.parentElement).toHaveClass('layouts__centered__body');
});