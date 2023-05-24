import { render } from '@testing-library/react';
import { SidebarLayout } from '../../ui/layouts';
import '@testing-library/jest-dom/extend-expect';

const props ={
    header: <p>header</p>,
    sidebar: <p>sidebar</p>,
    body: <p>body</p>,
    footer: <p>footer</p>
}
it('Renders the SidebarLayout', () => {
   const {getByText} = render(<SidebarLayout {...props}/>);

   const header = getByText(/header/i)
   expect(header.parentElement).toBeInTheDocument();
});

it('Has the class layouts__page', () => {
    const {getByText} = render(<SidebarLayout {...props}/>);

    const header = getByText(/header/i)
    expect(header.parentElement).toHaveClass('layouts__page');
});
