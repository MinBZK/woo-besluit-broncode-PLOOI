import { ListItemAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: ListItemAtom,
    args: { 
        children: "Lijst Item",
        link: false
    },
} as ComponentMeta<typeof ListItemAtom>

export const ListItem: ComponentStory<typeof ListItemAtom> = (args: any) => <CenteredLayout>
    <ListItemAtom {...args} />
</CenteredLayout>