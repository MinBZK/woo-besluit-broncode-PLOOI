import { render } from '@testing-library/react';
import { SmallLayout } from '../../ui/layouts';
import '@testing-library/jest-dom/extend-expect';

const props ={
    header: <p>header</p>,
    body: <p>body</p>,
    footer: <p>footer</p>
}
it('Renders the SidebarLayout', () => {
   const {getByText} = render(<SmallLayout {...props}/>);

   const header = getByText(/header/i)
   expect(header.parentElement).toBeInTheDocument();
});
