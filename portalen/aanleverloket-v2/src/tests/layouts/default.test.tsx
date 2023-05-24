import { render } from '@testing-library/react';
import { DefaultLayout } from '../../ui/layouts';
import '@testing-library/jest-dom/extend-expect';

const props ={
    header: <p>header</p>,
    body: <p>body</p>,
    footer: <p>footer</p>
}
it('Renders the DefaultLayout', () => {
   const {getByText} = render(<DefaultLayout {...props}/>);

   const header = getByText(/header/i)
   expect(header.parentElement).toBeInTheDocument();
});

it('Has the class layouts__page', () => {
    const {getByText} = render(<DefaultLayout {...props}/>);

    const header = getByText(/header/i)
    expect(header.parentElement).toHaveClass('layouts__page');
});
