import "public/fonts/style.css";
import "../styles/globals.css";
import React, { FC } from "react";
import { SessionProvider } from "next-auth/react";
import { Provider } from "react-redux";
import { AppProps } from "next/app";
import type { Session } from "next-auth";
import { wrapper } from "../features/store";
import { MobileBottom, MobileHeader } from "../components/oranisms";

// Use of the <SessionProvider> is mandatory to allow components that call
// `useSession()` anywhere in your application to access the `session` object.
// 현재의 형태에 적용하기 위해 공식 레퍼런스들과 다르게 작성한 부분들이 있음.
const MyApp: FC<AppProps<{ session: Session }>> = ({
  Component,
  pageProps,
}) => {
  const { store, props } = wrapper.useWrappedStore({
    ...pageProps,
    session: pageProps.session,
  });
  return (
    <SessionProvider session={pageProps.session}>
      <Provider store={store}>
        <MobileHeader />
        <Component {...props.pageProps} />
        <MobileBottom />
      </Provider>
    </SessionProvider>
  );
};

export default MyApp;
