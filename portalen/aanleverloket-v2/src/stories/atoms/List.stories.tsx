import { ListAtom, ListItemAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: ListAtom,
    args: {
        children: <ListItemAtom>Lege lijst</ListItemAtom>,
        unstyled: true,
        linked: true
    },
} as ComponentMeta<typeof ListAtom>

export const List: ComponentStory<typeof ListAtom> = (args: any) => <CenteredLayout>
    <ListAtom {...args} />
</CenteredLayout>