import { fireEvent, render, screen } from '@testing-library/react';
import { ListItemAtom } from '../../ui/atoms';
import '@testing-library/jest-dom/extend-expect';

const props = {
    link: false,
    bold: false,
};

it('Renders the ListItemAtom', () => {
   const {getByRole} = render(<ListItemAtom {...props}> lijst item </ListItemAtom>);

   const item = getByRole('listitem');
   expect(item).toBeInTheDocument();
});


it("Renders the child of ListItemAtom", () => {
    const { getByRole, getByText } = render(<ListItemAtom {...props}> <p>child</p> </ListItemAtom>);
  
    const list = getByRole('listitem');
    expect(list?.childElementCount).toBe(1);
    expect(list.hasChildNodes()).toBe(true);
  
    const child = getByText("child");
    expect(child).toBeInTheDocument();
  });
  
  it('Has the class list', () => {
     const {getByRole} = render(<ListItemAtom {...props}> <p>child</p> </ListItemAtom>);
  
     const list = getByRole("listitem");
     expect(list).toHaveClass('list_item');
  });
  
  it('Has the class list_item__linked', () => {
     const {getByRole} = render(<ListItemAtom {...props} link> <p>child</p> </ListItemAtom>);
  
     const list = getByRole("listitem");
     expect(list).toHaveClass('list_item__linked');
  });
  
  it('Has the class list_item__bold', () => {
     const {getByRole} = render(<ListItemAtom {...props} bold> <p>child</p> </ListItemAtom>);
  
     const list = getByRole("listitem");
     expect(list).toHaveClass('list_item__bold');
  });