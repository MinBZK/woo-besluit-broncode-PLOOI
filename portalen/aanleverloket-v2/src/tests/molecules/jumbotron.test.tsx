import { render } from '@testing-library/react';
import { JumbotronMolecule, ToastMolecule } from '../../ui/molecules';
import '@testing-library/jest-dom/extend-expect';

const props = {
    // subheader?: string;
    header: "title",
    intro: "intro text",
    // link?: any;
    // center?: boolean;
};

it('Renders the JumbotronMolecule', () => {
   const {getByRole} = render(<JumbotronMolecule {...props}/>);

   const title = getByRole('heading', { name: /title/i })
   expect(title.parentElement).toBeInTheDocument();
});